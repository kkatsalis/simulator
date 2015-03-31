echo
echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%"
echo "BEGIN SIMULATIONS"
echo "%%%%%%%%%%%%%%%%%%%%%%%%%%%"
echo
echo
echo "%%%%%%%%%%%%%%%%%%%%"
echo "BEGIN NTMS SIMULATIONS"
echo "%%%%%%%%%%%%%%%%%%%%"

x=1
y=5
while [ $x -le $y ]
do
java -jar "ESB.jar" NTMS $x wait
#echo "simulation finished: NTMS $x "
  x=$(( $x + 1 ))
done
echo
echo "%%%% NTMS SIMULATIONS FINISHED %%%%"
echo 
echo
echo
echo "%%%%%%%%%%%%%%%%%%%%"
echo "BEGIN WRR SIMULATIONS"
echo "%%%%%%%%%%%%%%%%%%%%"
x=1
while [ $x -le $y ]
do
java -jar "ESB.jar" WRR $x wait
#echo "simulation finished: WRR $x "
  x=$(( $x + 1 ))
done
echo
echo "%%%% WRR SIMULATIONS FINISHED %%%%"
echo
echo
echo
echo "%%%%%%%%%%%%%%%%%%%%"
echo "BEGIN Credit SIMULATIONS"
echo "%%%%%%%%%%%%%%%%%%%%"
x=1
while [ $x -le $y ]
do
java -jar "ESB.jar" Credit $x wait
#echo "simulation finished: Credit $x "
  x=$(( $x + 1 ))
done
echo
echo "%%%%% Credit SIMULATIONS FINISHED %%%%"
echo
echo
echo
echo "%%%%%%%%%%%%%%%%%%%%"
echo "BEGIN NonSaturatedBGP SIMULATIONS"
echo "%%%%%%%%%%%%%%%%%%%%"
x=1
while [ $x -le $y ]
do
java -jar "ESB.jar" NonSaturatedBGP $x wait
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
