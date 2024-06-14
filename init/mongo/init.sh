#!/bin/bash

openssl rand -base64 700 > /tmp/file.key
chmod 400 /tmp/file.key
