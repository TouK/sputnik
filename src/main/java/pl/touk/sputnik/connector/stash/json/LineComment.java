package pl.touk.sputnik.connector.stash.json;


/*
{
    "id": 1,
    "version": 1,
    "text": "[scalastyle] ERROR: error message",
    "author": {
        "name": "jcitizen",
        "emailAddress": "jane@example.com",
        "id": 101,
        "displayName": "Jane Citizen",
        "active": true,
        "slug": "jcitizen"
    },
    "createdDate": 1393297149981,
    "updatedDate": 1393297149981,
    "comments": [],
    "permittedOperations": {
        "editable": true,
        "deletable": true
    }
}
 */
public class LineComment {
    public int id;
    public String text;
}
