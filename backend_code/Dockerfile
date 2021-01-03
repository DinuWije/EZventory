FROM python:3.7

RUN apt update && \
    apt install -y netcat-openbsd

ENV INSTALL_PATH /Photos-Docker-Flask

ENV MYSQL_PWD password

RUN echo "mysql-server mysql-server/root_password password $MYSQL_PWD" | debconf-set-selections

RUN echo "mysql-server mysql-server/root_password_again password $MYSQL_PWD" | debconf-set-selections

RUN mkdir -p $INSTALL_PATH

WORKDIR $INSTALL_PATH

COPY requirements.txt requirements.txt

RUN pip install -r requirements.txt

COPY . .

RUN chmod +x entrypoint.sh

EXPOSE 80

CMD ["/bin/bash", "entrypoint.sh"]