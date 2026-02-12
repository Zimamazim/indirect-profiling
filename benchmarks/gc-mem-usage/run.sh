KONAN=/home/martinzimen/.konan/kotlin-native-prebuilt-linux-x86_64-2.3.0/bin/kotlinc-native

echo Compiling
$KONAN benchmark.kt -o benchmark.kexe

echo Running
./benchmark.kexe > out.csv &
MAIN_PID=$!
top -b -p $MAIN_PID -d 0.1 -w 512 | grep --line-buffered benchmark.kexe | awk '{print $5","$6; fflush()}' > mem.csv &
MONITOR_PID=$!
wait $MAIN_PID
kill $MONITOR_PID

echo Done
