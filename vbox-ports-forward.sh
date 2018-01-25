#!/bin/bash

# forward prots of VirtualBox to local host.
 
VBoxManage modifyvm "springms" --natpf1 "tcp-port3306,tcp,localhost,3306,,3306"
VBoxManage modifyvm "springms" --natpf1 "tcp-port3307,tcp,localhost,3307,,3307"
VBoxManage modifyvm "springms" --natpf1 "tcp-port5432,tcp,localhost,5432,,5432"
VBoxManage modifyvm "springms" --natpf1 "tcp-port5672,tcp,localhost,5672,,5672"
VBoxManage modifyvm "springms" --natpf1 "tcp-port15672,tcp,localhost,15672,,15672"
VBoxManage modifyvm "springms" --natpf1 "tcp-port6379,tcp,localhost,6379,,6379"
VBoxManage modifyvm "springms" --natpf1 "tcp-port27017,tcp,localhost,27017,,27017"
VBoxManage modifyvm "springms" --natpf1 "tcp-port9999,tcp,localhost,9999,,9999"
VBoxManage modifyvm "springms" --natpf1 "tcp-port8761,tcp,localhost,8761,,8761" 
VBoxManage modifyvm "springms" --natpf1 "tcp-port80,tcp,localhost,80,,80" 
VBoxManage modifyvm "springms" --natpf1 "tcp-port8091,tcp,localhost,8091,,8091" 
VBoxManage modifyvm "springms" --natpf1 "tcp-port8092,tcp,localhost,8092,,8092" 
VBoxManage modifyvm "springms" --natpf1 "tcp-port8093,tcp,localhost,8093,,8093" 
VBoxManage modifyvm "springms" --natpf1 "tcp-port8094,tcp,localhost,8094,,8094" 
VBoxManage modifyvm "springms" --natpf1 "tcp-port11210,tcp,localhost,11210,,11210" 

