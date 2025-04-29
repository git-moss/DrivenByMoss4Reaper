export JAVA_HOME=/Users/mos/Documents/Development/Jdk-x64/jdk-24.0.1+9/Contents/Home
export DESTINATION_FOLDER=macos/java-runtime
rm -drf $DESTINATION_FOLDER
$JAVA_HOME/bin/jlink --no-header-files --no-man-pages --strip-debug --add-modules java.desktop,java.instrument,java.management,java.naming,java.sql,java.logging,jdk.accessibility --output $DESTINATION_FOLDER
