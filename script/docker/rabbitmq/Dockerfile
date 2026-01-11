FROM rabbitmq:4.2.0-management

LABEL maintainer="gova"

ADD ./plugins/rabbitmq_delayed_message_exchange-4.2.0.ez /plugins

RUN rabbitmq-plugins enable rabbitmq_delayed_message_exchange

ENTRYPOINT ["rabbitmq-server"]