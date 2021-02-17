export JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-15.jdk/Contents/Home
export DESTINATION_FOLDER=macos-arm/java-runtime
rm -drf $DESTINATION_FOLDER
$JAVA_HOME/bin/jlink --no-header-files --no-man-pages --compress=1 --strip-debug --add-modules java.desktop,java.instrument,java.management,java.naming,java.sql,java.logging,jdk.accessibility --output $DESTINATION_FOLDER
