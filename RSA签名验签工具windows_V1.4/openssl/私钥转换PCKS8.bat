bin\openssl.exe genrsa -out rsa_private_key.pem 1024
bin\openssl.exe rsa -in rsa_private_key.pem -pubout -out rsa_public_key.pem
bin\openssl.exe pkcs8 -topk8 -inform PEM -in rsa_private_key.pem -outform PEM -nocrypt > rsa_private_key_pkcs8.pem