FROM registry:2 

#ENV 	 REGISTRY_HTTP_ADDR 0.0.0.0:5000
ENV      REGISTRY_HTTP_TLS_CERTIFICATE /certs/domain.crt
ENV      REGISTRY_HTTP_TLS_KEY /certs/domain.key
ENV      REGISTRY_AUTH htpasswd
ENV      REGISTRY_AUTH_HTPASSWD_PATH /auth/htpasswd
ENV      REGISTRY_AUTH_HTPASSWD_REALM "Registry Realm"

# RUN mkdir /certs
COPY certs /certs
COPY auth /auth
