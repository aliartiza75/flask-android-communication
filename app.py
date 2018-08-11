#!flask/bin/python
from flask import Flask
import json
app = Flask(__name__)

tasks = [
    {
        'id': 1,
        'title': u'Flask API',
        'description': u'It is an rest api build in flask', 
        'done': False
    },
]


@app.route('/')
def index():
    return json.dumps(tasks)

if __name__ == '__main__':
    app.run(debug=True)

