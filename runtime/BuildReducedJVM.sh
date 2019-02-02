export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.2+9/Contents/Home
export DESTINATION_FOLDER=macos/java-runtime
rm -drf $DESTINATION_FOLDER
$JAVA_HOME/bin/jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.desktop,java.xml,java.naming,jdk.accessibility --output $DESTINATION_FOLDER
