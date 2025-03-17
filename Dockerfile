# Use Node.js as base image
FROM node 

# Set environment variables for MongoDB
ENV MONGO_DB_USERNAME=admin \
    MONGO_DB_PWD=qwerty

# Create application directory
WORKDIR /testapp

# Copy all project files into the container
COPY . .

# Install project dependencies
RUN npm install

# Expose the application port
EXPOSE 5050

# Start the Node.js application
CMD ["node", "server.js"]
