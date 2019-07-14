export JAVA_HOME=/home/mos/java/jdk-11.0.3+7
export DESTINATION_FOLDER=linux/java-runtime
rm -drf $DESTINATION_FOLDER
$JAVA_HOME/bin/jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.desktop,java.xml,java.naming,jdk.accessibility --output $DESTINATION_FOLDER
