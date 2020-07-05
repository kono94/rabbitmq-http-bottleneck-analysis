const express = require("express");
const bodyParser = require('body-parser');
const app = express();
app.use(bodyParser.urlencoded({extended: false}));
app.use(bodyParser.json());

const uuidv4 = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        let r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
};

app.post("/endpoint", (req, res, next) => {
    console.log(`${PORT} has received: ${JSON.stringify(req.body)}`.padStart(50));
    const a = [];
    for (let i = 0; i < 200; ++i) {
        a[i] = uuidv4();
    }
    res.status(200);
    res.send({success: true});
});


const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});
