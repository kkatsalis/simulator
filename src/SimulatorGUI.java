/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import BL.Configuration;
import BL.Simulator;
import Enumerators.*;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import java.io.*;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 *
 * @author maevious
 */
@SuppressWarnings("serial")
public class SimulatorGUI extends ApplicationFrame 
{

   static Configuration _config;
   static Simulator _simulator;
  
   static double gA;
   static double gB;
   static int la;  
   static int lb; 
   static String filename;
   boolean plotDeviation=false;
   static String schedulingAlgorithm=null;
   static String simulationID;
   
      public SimulatorGUI(final String title) throws IOException {
        super(title);
      
        int numberOfDomains=_config.getNumberOfDomains();
        
        try {
            BufferedReader CSVFile = new BufferedReader(new FileReader(title));
                
        
        double time=0;
        
        String dataRow = CSVFile.readLine(); 
        String[] tempArray = dataRow.split("&");	
        
        numberOfDomains=tempArray.length-1;
        
        final XYSeriesCollection dataset = new XYSeriesCollection();
        final XYSeries[] series=new XYSeries[numberOfDomains];
        
        
        for (int i = 0; i < numberOfDomains; i++) {
            series[i] = new XYSeries("SD"+String.valueOf(i));
        }
        
        
        dataRow = CSVFile.readLine(); // Read next line of data.
        
         
         while (dataRow != null){
            
             String[] dataArray = dataRow.split("&");
            
             time=Double.valueOf(dataArray[0]);
           
             if(plotDeviation)
            	 for (int i = 0; i < series.length; i++) {
            		 series[i].add(time,Double.valueOf(dataArray[i+1]) );
            	 }
             else
            	 for (int i = 0; i < series.length-1; i++) {
            		 series[i].add(time,Double.valueOf(dataArray[i+1]) );
            	 }
           
    
             dataRow = CSVFile.readLine(); // Read next line of data.
        }
  
         // Close the file once all data has been read.
            CSVFile.close();
  

        for (int i = 0; i < series.length; i++) {
            dataset.addSeries(series[i]);
        }
       
        //Calculate Integral per domain
        integral(series);
        
        
        final JFreeChart chart = ChartFactory.createXYLineChart(title,"Time", "CPU Utilization", dataset,PlotOrientation.VERTICAL,true,true,false);
        chart.setBackgroundPaint(Color.white);
        
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.getRenderer().setSeriesPaint(0, Color.BLACK);
        plot.getRenderer().setSeriesPaint(1, Color.RED);
        plot.getRenderer().setSeriesPaint(2, Color.BLUE);
      //  plot.getRenderer().setSeriesPaint(3, Color.MAGENTA);
        
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setRangeMinorGridlinesVisible(true);
        
        //plot.setRangeMinorGridlinePaint(Color.BLACK);
        //Enable shapes on the line chart
         //plot.getRenderer().setSeriesStroke(0, new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,2.0f, new float[] {0.01f, 6.0f}, 0.0f));
         //plot.getRenderer().setSeriesStroke(1, new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,2.0f, new float[] {0.01f, 6.0f}, 0.0f));
         //plot.getRenderer().setSeriesStroke(2, new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,2.0f, new float[] {0.01f, 6.0f}, 0.0f));
         //plot.getRenderer().setSeriesStroke(3, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,2.0f, new float[] {0.01f, 6.0f}, 0.0f));
      
   
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(450, 350));
        
  
        
        setContentPane(chartPanel);

        } 
        catch (FileNotFoundException ex) {
            Logger.getLogger(SimulatorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * @param args the command line arguments
     */
     public static void main(String[] args) throws Exception {
             
       

    	 if(args.length>0)
    	 schedulingAlgorithm=args[0];  
         if(args.length>1)
    	 simulationID=args[1];  
    	
       
       
        _config=new Configuration();
        loadSimulationParameters(_config,schedulingAlgorithm);  
        configureTheFileName();
        _config.CheckConfiguration();
        _simulator=new Simulator(_config);
        _simulator.ConfigureSimulator();
        _simulator.getSla().SLAConfiguration();
        
        _simulator.Run();        
         
       
//       _simulator.getDataCenter().getController().getStatisticsExporter().OutputConvergenceStatistics("Router");
//       _simulator.getDataCenter().getController().getStatisticsExporter().OutputServiceTimeStatistics();
      
//      outputTotheConsole(); 
      outputToGraphic(); 
//      outputFromTheGraphic();    
    
    }
    private static void configureTheFileName(){
    	
    	 filename=String.valueOf(schedulingAlgorithm+"_"+simulationID+".csv");
    
         _config.setFileName(filename);  
    	
    }
    private static void outputTotheConsole(){
//    	 System.out.println("--------Controller-------");
//    	 System.out.println(_config.getConvergenceTime());
    //   System.out.println(_simulator.getDataCenter().getNode().get(1).get_nodeStatistics().getCpuStatistics().getCpuUtilization_PSD()[0]);
     //   System.out.println(_simulator.getDataCenter().getNode().get(1).get_nodeStatistics().getCpuStatistics().getCpuUtilization_PSD()[1]);
       
//         System.out.println("--------Router-------");
//         System.out.println(_simulator.getDataCenter().getNode().get(0).getNodeController().get_convergentValuesPSD()[0]);
//         System.out.println(_simulator.getDataCenter().getNode().get(0).getNodeController().get_convergentValuesPSD()[1]);
////       System.out.println(_simulator.getDataCenter().getNode().get(0).getNodeController().get_convergentValuesPSD()[2]);
//         System.out.println(_simulator.getDataCenter().getNode().get(0).getNodeController().getNumberOfSchedulingRounds());
//           int magicRound=  _simulator.getDataCenter().getNode().get(0).getNodeController().getRoundOfConvergence();
//    	   double timeOfMagic=_config.getConvergenceTime();
//    	   boolean convergence=_simulator.getDataCenter().getController().isFinalConvergence();
//           System.out.println(magicRound+" & "+timeOfMagic+" & "+convergence);
           
//         System.out.println("----------Appliance-----");
//         System.out.println(_simulator.getDataCenter().getNode().get(1).getNodeController().get_convergentValuesPSD()[0]);
//         System.out.println(_simulator.getDataCenter().getNode().get(1).getNodeController().get_convergentValuesPSD()[1]);
//         System.out.println(_simulator.getDataCenter().getNode().get(1).getNodeController().get_convergentValuesPSD()[2]);
//        
//         System.out.println("---------------");
    	 
     }
    private static void outputToGraphic(){
    	
    	 try {
             
             final SimulatorGUI myGraph;
             myGraph = new SimulatorGUI(filename);
            
             if(false){
                myGraph.pack();
                RefineryUtilities.centerFrameOnScreen(myGraph);
                myGraph.setVisible(true);
             }

         } catch (IOException ex) {
             Logger.getLogger(SimulatorGUI.class.getName()).log(Level.SEVERE, null, ex);
         }
    	
    }
    private static void outputFromTheGraphic()
    {
    	//     ChartUtilities.saveChartAsPNG(new File(title+".png"), chart, 450, 350);
        
        try {
        //       writeChartToPDF(chart,450,350,title);
        } 
        catch (Exception ex) {
            
        Logger.getLogger(SimulatorGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    	
    }
    @SuppressWarnings("unchecked")
    private static void loadSimulationParameters(Configuration _config,String schedulingAlgorithm) {

      
        _config.loadConfigurationParameters();
        _config.loadNodeConfigurationParameters(schedulingAlgorithm);
        _config.CreateHashMaps();
       
        _config.loadRouterRatesParameters();
        _config.loadApplianceRatesParameters();
        _config.loadBurstConfigurationParameters();
 
    }

   
    
    private static void writeChartToPDF(JFreeChart chart, int width, int height, String fileName) {
    PdfWriter writer = null;
 
    Document document = new Document();
 
    try {
        writer = PdfWriter.getInstance(document, new FileOutputStream(fileName+".pdf"));
        document.open();
        PdfContentByte contentByte = writer.getDirectContent();
        PdfTemplate template = contentByte.createTemplate(width, height);
        Graphics2D graphics2d = template.createGraphics(width, height,
                new DefaultFontMapper());
        Rectangle2D rectangle2d = new Rectangle2D.Double(0, 0, width,height);
 
        chart.draw(graphics2d, rectangle2d);
 
        graphics2d.dispose();
        contentByte.addTemplate(template, 0, 0);
 
    } catch (Exception e) {
        e.printStackTrace();
    }
    document.close();
    }

    private void integral(XYSeries[] series) {
       
        double[] integralPSD=new double[_config.getNumberOfDomains()];
        double x1,x2,y1,y2;
        double[] slaIntegral=new double[_config.getNumberOfDomains()];
        double totalDifference=0;
        
        for (int i = 0; i < _config.getNumberOfDomains(); i++) {
            for (int j = 1; j < series[i].getItemCount(); j++) {
                x1=series[i].getX(j-1).doubleValue();
                x2=series[i].getX(j).doubleValue();
                y1=series[i].getY(j-1).doubleValue();
                y2=series[i].getY(j).doubleValue();
                integralPSD[i]+=area(x2-x1,y1,y2);
               
            }
            slaIntegral[i]=_simulator.getSla().getCpuUtilizationSLA()[i]*_config.getSimulationTime();
            double difference=integralPSD[i]-slaIntegral[i];
            totalDifference+=Math.abs(difference);
          //    System.out.println(i+"-"+integralPSD[i]+"- sla: "+slaIntegral[i]+"- difference: "+difference);
              
        }
    
        System.out.println(totalDifference);
      
        
    }
    
    private static double  area(double x, double y1, double y2){
    
        double area=0;
    
        area=x*(y1/2+y2/2);
                
        return area;
    }
    
}
