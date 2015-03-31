echo
echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%"
echo "BEGIN SIMULATIONS"
echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%"
echo
echo

x=1
y=20

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

echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%"
echo "ALL SIMULATIONS FINISHED"
echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%"
echo
echo
