// service name is exchange name
const exchange = "job_service";

// events from this service as topic
const topic = "message.sent";

const queueName = "node_subscriber_message_receiving";

(async function () {
    let conn;
    try {
        conn = await require("amqplib").connect(`amqp://${process.env.RABBIT_USER}:${process.env.RABBIT_PW}@${process.env.RABBIT_HOST}`);
    } catch (e) {
        console.error(e);
        console.log("Connection failed to establish, shutting down");
        process.exit(3);
    }

    const channel = await conn.createChannel();

    // pulling and processing max. of 50 messages
    channel.prefetch(50);

    // generate exchange of type 'topic'
    channel.assertExchange(exchange, 'topic', {
        durable: true
    });

    // create a durable queue
    const q = await channel.assertQueue(queueName, {noAck: false, durable: true});

    // bind queue to specific exchange and topic
    channel.bindQueue(q.queue, exchange, topic);

    // what to do if a message is received
    channel.consume(q.queue, (msg) => {
        try {
            processMsg(msg);
            channel.ack(msg);
        } catch (e) {
            console.error(e);
            channel.nack(msg)
        }
    });

}());

function processMsg(msg) {
    if (msg !== null) {
        console.log(msg.content.toString());
    } else {
        throw Error("msg null")
    }
    const a = [];
    for (let i = 0; i < 200; ++i) {
        a[i] = uuidv4();
    }
}

const uuidv4 = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        let r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
};

