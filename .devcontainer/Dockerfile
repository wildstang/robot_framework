FROM mcr.microsoft.com/devcontainers/java:1-17-bookworm

# install dependencies
RUN apt update
RUN apt install -y python3-pip
RUN pip3 install --break-system-packages requests

# download WPILib extension
COPY ./get-wpilib.py /opt
RUN python3 /opt/get-wpilib.py