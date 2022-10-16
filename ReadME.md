To create collection:
db.createCollection("body")

To add constraint:
db.body.createIndex({"body":""},{unique:true,dropDups: true})