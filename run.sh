#!/bin/bash

#!/bin/bash

# Checking if the user passed an operation type
#if [ $# -lt 2 ]; then
#    echo "Usage command: CREATE_BILL, LIST_BILL, PAY, LIST_PAYMENT, SCHEDULE, "
#    exit 1
#fi

operation=$1
echo "Args: $*"
# Handling the operation
case $operation in
    CREATE_BILL)
        type=$2
        amount=$3
        provider=$4
        duedate=$5
        mvn -X clean install exec:java -Dexec.mainClass="org.example.PaymentApp" -Dexec.args="CREATE_BILL $type $amount $provider $duedate"
        ;;
    LIST_BILL)
        mvn -X clean install exec:java -Dexec.mainClass="org.example.PaymentApp" -Dexec.args="LIST_BILL"
        ;;
    PAY)
        allArgs=${@:2}
        mvn -X clean install exec:java -Dexec.mainClass="org.example.PaymentApp" -Dexec.args="PAY $allArgs"
        ;;
    LIST_PAYMENT)
        mvn -X clean install exec:java -Dexec.mainClass="org.example.PaymentApp" -Dexec.args="LIST_PAYMENT"
        ;;
    SCHEDULE)
       billId=$2
       newDueDate=$3ß
       mvn -X clean install exec:java -Dexec.mainClass="org.example.PaymentApp" -Dexec.args="SCHEDULE $billId $newDueDate"
       ;;
    SEARCH_BILL_BY_PROVIDER)
       provider=$2
       mvn -X clean install exec:java -Dexec.mainClass="org.example.PaymentApp" -Dexec.args="SEARCH_BILL_BY_PROVIDER $provider"
       ;;
esac

