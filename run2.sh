echo
echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%"
echo "BEGIN SIMULATIONS"
echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%"
echo
echo
x=1
y=1
while [ $x -le 500 ]
do
while [ $y -le 1 ]
do
java -jar "Simulator.jar" NTMS $x wait
y=$(( $y + 1 ))
done
echo "simulation $x finished"
  x=$(( $x + 1 ))
  y=1
done
echo
echo "%%%%%%%%%%%%%%%%%%%%"
echo "SIMULATIONS FINISHED"
