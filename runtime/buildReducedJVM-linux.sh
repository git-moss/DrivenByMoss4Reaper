export JAVA_HOME=/home/mos/java/jdk-16+36/
export DESTINATION_FOLDER=linux/java-runtime
rm -drf $DESTINATION_FOLDER
$JAVA_HOME/bin/jlink --no-header-files --no-man-pages --compress=1 --strip-debug --add-modules java.desktop,java.instrument,java.management,java.naming,java.sql,java.logging,jdk.accessibility --output $DESTINATION_FOLDER
