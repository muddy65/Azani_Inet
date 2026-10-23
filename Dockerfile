# Stage 1: Compile the plain Java application
FROM eclipse-temurin:17-alpine AS build
WORKDIR /app

# Copy all files from your repository into the container
COPY . .

# Create a clean directory for our compiled production classes
RUN mkdir -p out

# Find all Java source files and compile them into the 'out' directory
RUN find src -name "*.java" > sources.txt && \
    javac -d out @sources.txt

# Stage 2: Minimal runtime environment
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the compiled .class files from the build stage
COPY --from=build /app/out /app/out

# If your project uses external libraries inside the 'lib' folder, copy them too
COPY --from=build /app/lib /app/lib

# Expose your application port
EXPOSE 8080

# Run your main class directly from its package structure
# (Using the classpath option to include your compiled classes and dependencies)
ENTRYPOINT ["java", "-cp", "out:lib/*", "com.azani.ui.MainDashboard"]
