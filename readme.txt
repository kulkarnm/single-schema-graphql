Input
query($accountNumber:String) {
    getAccountByAccountNumber(accountNumber: $accountNumber) {
        accountNumber
    }
}

query variable
{
    "accountNumber" : "1"
}

output
{
    "meta": {
        "empty":false
    },
    "data" : {
        "getAccountByAccountNumber" : {
            "accountNumber" : "1"
        }
    }
}