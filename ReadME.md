To create collection:
db.createCollection("body")

To add constraint:
db.body.createIndex({"body":"text"},{unique:true,dropDups: true})