#!/bin/bash

# This file is copied from Docker Labs, https://github.com/docker/labs
# 1. added registry-mirror for Chinese users.
# 2. added virtualbox limit options.
# 3. decreased the number of managers to 2

# Swarm mode using Docker Machine

managers=2
workers=3

# create manager machines
echo "======> Creating $managers manager machines ...";
for node in $(seq 1 $managers);
do
	echo "======> Creating manager$node machine ...";
	docker-machine create -d virtualbox --virtualbox-cpu-count 1 --virtualbox-memory "1024" --virtualbox-disk-size "5000"  --engine-registry-mirror https://registry.docker-cn.com manager$node;
done

# create worker machines
echo "======> Creating $workers worker machines ...";
for node in $(seq 1 $workers);
do
	echo "======> Creating worker$node machine ...";
	docker-machine create -d virtualbox --virtualbox-cpu-count 1 --virtualbox-memory "1024" --virtualbox-disk-size "5000" --engine-registry-mirror https://registry.docker-cn.com worker$node;
done

# list all machines
docker-machine ls

# initialize swarm mode and create a manager
echo "======> Initializing first swarm manager ..."
docker-machine ssh manager1 "docker swarm init --listen-addr $(docker-machine ip manager1) --advertise-addr $(docker-machine ip manager1)"

# get manager and worker tokens
export manager_token=`docker-machine ssh manager1 "docker swarm join-token manager -q"`
export worker_token=`docker-machine ssh manager1 "docker swarm join-token worker -q"`

echo "manager_token: $manager_token"
echo "worker_token: $worker_token"

# other masters join swarm
for node in $(seq 2 $managers);
do
	echo "======> manager$node joining swarm as manager ..."
	docker-machine ssh manager$node \
		"docker swarm join \
		--token $manager_token \
		--listen-addr $(docker-machine ip manager$node) \
		--advertise-addr $(docker-machine ip manager$node) \
		$(docker-machine ip manager1)"
done

# show members of swarm
docker-machine ssh manager1 "docker node ls"

# workers join swarm
for node in $(seq 1 $workers);
do
	echo "======> worker$node joining swarm as worker ..."
	docker-machine ssh worker$node \
	"docker swarm join \
	--token $worker_token \
	--listen-addr $(docker-machine ip worker$node) \
	--advertise-addr $(docker-machine ip worker$node) \
	$(docker-machine ip manager1)"
done

# show members of swarm
docker-machine ssh manager1 "docker node ls"

