#!/bin/csh

# $Header: Exp $
#
# postfetch.sh
#
# Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
#
#    NAME
#      postfetch.sh - script to be executed post fetching transactions
#
#    DESCRIPTION
#      <short description of component this file declares/defines>
#t
#    NOTES
#      <other useful comments, qualifications, etc.>
#
#    MODIFIED   (MM/DD/YY)
#    utulasi    17/04/17 - Created.

#set -x

echo "Start Time : `date`"
while ( 1 == 1 )
if ( -f /home/utulasi/mitra/ADEVIEW/lok.lok) then
  echo "Lock Exists, will wait for one more minute"
  sleep 60
else
  echo "No Lock Exists, Proceeding with the job"
  break
endif
end
echo ""
echo ""
(crontab -l ; echo "1 * * * * $HOME/mitra/ADEVIEW/postfetch.sh") | grep -v 'no crontab' | grep -v "$HOME/mitra/ADEVIEW/postfetch.sh" |  sort | uniq | crontab -
cat $HOME/mitra/ADEVIEW/jobstartmail.txt | mutt -e 'my_hdr From: DOS-MITRA <noreply@oracle.com>' -s 'Started Cron Job For Target Window ADEVIEW' INTEGRATORS -c umasankar.tulasi@oracle.com;
cat $HOME/mitra/ADEVIEW/TEMPNAME.txt | /usr/dev_infra/platform/bin/ade useview ADEVIEW;
echo ""
echo ""
echo "End Time  : `date`"
cat $HOME/mitra/ADEVIEW/jobendmail.txt | mutt -e 'my_hdr From: DOS-MITRA <noreply@oracle.com>' -s 'Completed Cron Job For Target Window ADEVIEW' INTEGRATORS -c umasankar.tulasi@oracle.com
