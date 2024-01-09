#!/bin/bash

# Set variables
api_url="http://10.20.229.55/api/patients/1/temperatures"
bearer_token="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkiLCJpYXQiOjE3MDQ3MjgyMjUsImV4cCI6MTcwNDczMTgyNX0.9zfqcwTOkYQyp_fImnKpgiP7w8nmO1YzfkdKmxMyivU"

# Loop for 20 POST requests with random temperature values ranging from 35 to 45
for ((i = 0; i < 20; i++)); do
    random_temperature=$((RANDOM % (45 - 35 + 1) + 35))
    json_body="{\"temperature\":$random_temperature}"
    
    # Make POST request using curl
    curl -X POST -H "Authorization: Bearer $bearer_token" -H "Content-Type: application/json" -d "$json_body" "$api_url"
done

