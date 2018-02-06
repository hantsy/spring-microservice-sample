# Set nginx base image
FROM nginx:alpine 

RUN mkdir /etc/nginx/ssl  
COPY ssl /etc/nginx/ssl 

# Copy custom configuration file from the current directory
COPY nginx.conf /etc/nginx/conf.d/default.conf
  
#COPY www /usr/share/nginx/www  
#COPY archive /usr/share/nginx/archive


