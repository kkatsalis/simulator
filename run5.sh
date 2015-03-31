echo
echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%"
echo "BEGIN SIMULATIONS"
echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%"
echo
echo

x=1
y=5
echo "%%%%%%%%%%%%%%%%%%%%"
echo "BEGIN WRR SIMULATIONS"
echo "%%%%%%%%%%%%%%%%%%%%"
x=1
while [ $x -le $y ]
do
java -jar "SimulatorGeneral.jar" WRR $x wait
#echo "simulation finished: WRR $x "
  x=$(( $x + 1 ))
done
echo
echo "%%%% WRR SIMULATIONS FINISHED %%%%"
echo
echo
echo
echo "%%%%%%%%%%%%%%%%%%%%"
echo "BEGIN NonSaturatedBGP SIMULATIONS"
echo "%%%%%%%%%%%%%%%%%%%%"
x=1
while [ $x -le $y ]
do
java -jar "SimulatorGeneral.jar" NonSaturatedBGP $x wait
#echo "simulation finished: NonSaturatedBGP $x "
  x=$(( $x + 1 ))
done
echo
echo "%%%% NonSaturatedBGP SIMULATIONS FINISHED %%%%"
echo

echo "%%%%%%%%%%%%%%%%%%%%"
echo "BEGIN NonSaturatedBGPK SIMULATIONS"
echo "%%%%%%%%%%%%%%%%%%%%"
x=1
while [ $x -le $y ]
do
java -jar "SimulatorGeneral.jar" NonSaturatedBGPK $x wait
#echo "simulation finished: NonSaturatedBGP $x "
  x=$(( $x + 1 ))
done
echo
echo "%%%% NonSaturatedBGPK SIMULATIONS FINISHED %%%%"

echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%"
echo "ALL SIMULATIONS FINISHED"
echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%"
echo
echo
