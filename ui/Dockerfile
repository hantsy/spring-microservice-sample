# Set nginx base image
FROM nginx

#RUN mkdir /etc/nginx/ssl
#COPY ssl /etc/nginx/ssl

# Copy custom configuration file from the current directory
COPY nginx.conf /etc/nginx/nginx.conf
COPY dist /usr/share/nginx/html
EXPOSE 3000

