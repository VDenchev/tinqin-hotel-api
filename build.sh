#!/bin/bash

services=(
"/home/user/IdeaProjects/TINQIN/hotel"
"/home/user/IdeaProjects/TINQIN/comments"
"/home/user/IdeaProjects/TINQIN/authentication"
"/home/user/IdeaProjects/TINQIN/bff"
"/home/user/IdeaProjects/TINQIN/admin"
)

build_project() {
    local service_path=$1
    echo "Building service in $service_path"
    cd "$service_path" || { echo "Failed to enter $service_path"; return 1; }
    mvn clean package -U -DskipTests &
    wait
    if [ $? -eq 0 ]; then
        echo "Build successful for $service_path"
    else
        echo "Build failed for $service_path"
    fi
}

for project in "${services[@]}"; do
    build_project "$project" &
done

wait

echo "All builds completed."