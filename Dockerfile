FROM tomcat:8.5
WORKDIR /opt/tomcat
ADD petclinic.war /usr/local/tomcat/webapps/petclinic.war