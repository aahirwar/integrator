package ust.tools.bot.integrator.model.util;


public interface Commands {
    
//    String PREPARE_VIEW_CMD = "ade lsviews | grep viewname || ade createview viewname -series seriesname -latest -noenv && ade useview viewname -noenv && exit";
    String PREPARE_VIEW_CMD = "ls -l /scratch/$USER/view_storage/ | grep viewname || ade createview viewname -series seriesname -latest -noenv && ade useview viewname -noenv && exit";

    String USEVIEW_CMD = "ade useview viewname -noenv && exit";
    
    String BEGIN_TRANS_REOPEN_CMD = "ade pwv | grep 'VIEW_TXN_NAME : NONE' && ade lstrans -like viewname -repos | grep viewname && ade begintrans -reopen viewname -no_restore";
    
    String BEGIN_TRANS_CMD = "ade pwv | grep 'VIEW_TXN_NAME : NONE' && ade begintrans viewname -no_restore";
    
    String DIR_CMD = "cp /net/slc11bnd.us.oracle.com/scratch/mitra/bin/*.* mitradir; mkdir -p mitradir/viewname; mkdir -p /net/slc11bnd.us.oracle.com/scratch/mitra/logs/viewname; cd $ADE_VIEW_ROOT";

    String FETCH_TRANS_CMD = "ade fetchtrans -repos transname -noask > mitradir/viewname/log.log";

    String ANALYSE_FETCH_CMD = "grep 'does not exist in backend' mitradir/viewname/log.log || grep 'conflicts are outstanding ' mitradir/viewname/log.log && ade aborttrans -purge -no_restore -force && ade begintrans -reopen viewname -no_restore";

    String SAVE_TRANS_CMD = "ade lsco | grep 'No files checked-out in view' || ade ciall -identical >> mitradir/viewname/log.log && grep 'Checked in version' mitradir/viewname/log.log && ade savetrans >> mitradir/viewname/log.log";

    String CAT_LOG_CMD = "cat mitradir/viewname/log.log";
    
    String MAIL_LOG_CMD = "cat mitradir/viewname/fetchtransemail-transname.txt | mutt -e 'my_hdr From: pcode-MITRA <noreply@oracle.com>' -a mitradir/viewname/log.log  -s 'Fetchtrans Log for transname  : environment' -c intgemails -- useremail";
    
    String MAIL_CONFLICTS_CMD = "cat mitradir/viewname/conflictsemail-transname.txt | mutt -e 'my_hdr From: pcode-MITRA <noreply@oracle.com>' -a mitradir/viewname/log.log -s 'Deployment Request With Non-Trivial Conflicts' -c useremail -- intgemails ";
    
    String CREATE_FETCH_EMAIL_CMD = "cp mitradir/fetchtransemail.txt mitradir/viewname/fetchtransemail-transname.txt && sed -ie 's/TXN/transname/g' mitradir/viewname/fetchtransemail-transname.txt && sed -ie 's/ENVNAME/environment/g' mitradir/viewname/fetchtransemail-transname.txt && sed -ie 's/ADEVIEW/viewname/g' mitradir/viewname/fetchtransemail-transname.txt";
    
    String CREATE_CONFLICT_EMAIL_CMD = "cp mitradir/conflictsemail.txt mitradir/viewname/conflictsemail-transname.txt && sed -ie 's/TXN/transname/g' mitradir/viewname/conflictsemail-transname.txt && sed -ie 's/ENVNAME/environment/g' mitradir/viewname/conflictsemail-transname.txt && sed -ie 's/ADEVIEW/viewname/g' mitradir/viewname/conflictsemail-transname.txt";
    
    String CREATE_LOG_CMD = "echo '------------------------------ Fetch Trans Log -------------------------------------' > mitradir/viewname/log.log";
    
    String CREATE_CRON_CMD = "cp mitradir/template.txt mitradir/viewname/template.txt && cp mitradir/postfetch.sh mitradir/viewname/postfetch.sh && sed -ie 's/ADEVIEW/viewname/g' mitradir/viewname/postfetch.sh && sed -ie 's/TEMPNAME/template/g' mitradir/viewname/postfetch.sh && sed -ie 's/PCODE/pcode/g' mitradir/viewname/postfetch.sh && sed -ie 's/INTEGRATORS/intgemails/g' mitradir/viewname/postfetch.sh";
    
    String CREATE_MAIL_CMD = "cp mitradir/jobstartmail.txt mitradir/viewname/jobstartmail.txt && sed -ie 's/ADEVIEW/viewname/g' mitradir/viewname/jobstartmail.txt && sed -ie 's/TEMPNAME/template/g' mitradir/viewname/jobstartmail.txt && cp mitradir/jobendmail.txt mitradir/viewname/jobendmail.txt && sed -ie 's/ADEVIEW/viewname/g' mitradir/viewname/jobendmail.txt && sed -ie 's/ADEVIEW/viewname/g' mitradir/viewname/SMC.txt && sed -ie 's/TEMPNAME/template/g' mitradir/viewname/jobendmail.txt";    
    
    String CRON_CMD = "(crontab -l ; echo \"minuteofhour hourofday dayofmonth monthofyear * mitradir/viewname/postfetch.sh > /net/slc11bnd.us.oracle.com/scratch/mitra/logs/viewname/Audit.log\") | grep -v 'no crontab' | sort | uniq | crontab -";
    
    String TIMER_POST_CUTOFF = "mitradir/viewname/postfetch.sh > /net/slc11bnd.us.oracle.com/scratch/mitra/logs/viewname/Audit.log";
    
    String DELETE_CRON = "(crontab -l ; echo \"1 * * * * mitradir/previousname/postfetch.sh\") | grep -v 'no crontab' | grep -v \"mitradir/previousname/postfetch.sh\" |  sort | uniq | crontab -";
    
    String EXIT_CMD = "exit";
    
    String TRANS_NAMES_CMD = "touch mitradir/viewname/TxnNames.txt; grep transname mitradir/viewname/TxnNames.txt || echo transname >> mitradir/viewname/TxnNames.txt";
    
    String CREATE_LOK_CMD = "touch mitradir/viewname/lok.lok";
    
    String DELETE_LOK_CMD = "rm -rf mitradir/viewname/lok.lok";
}


