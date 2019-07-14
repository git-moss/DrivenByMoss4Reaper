export JAVA_HOME=/Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home
export DESTINATION_FOLDER=macos/java-runtime
rm -drf $DESTINATION_FOLDER
$JAVA_HOME/bin/jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.desktop,java.xml,java.naming,jdk.accessibility --output $DESTINATION_FOLDER
