rm -r /usr/local/hadoop/tmp/


sudo mkdir -p /app/hadoop/tmp
sudo chown hadoop:hadoop /app/hadoop/tmp
sudo chmod 750 /app/hadoop/tmp
/usr/local/hadoop/bin/hadoop namenode -format
bin/start-all.sh 
