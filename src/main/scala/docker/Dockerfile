# Use the official MongoDB image
FROM mongo:latest

# Copy the initialization script into the Docker container
COPY init-mongo.js /docker-entrypoint-initdb.d/

# Expose MongoDB port
EXPOSE 27017
