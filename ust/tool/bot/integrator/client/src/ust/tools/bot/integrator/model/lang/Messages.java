package ust.tools.bot.integrator.model.lang;


public interface Messages {

    String MENU = "Please select the Service from the below options. \n1.Deployment Request.\t2.Deployments Summary.";

    String INTG_MENU =
        "Please select the Service from the below options. \n1.Deployment Request.\t2.Deployments Summary.\t3.Manage Deployments";

    String COMMON_OPTIONS = " Or Enter 9 to Restart from the beginning";

    String PROMPT_4_ENV = "Please select the Environment from the below options." + COMMON_OPTIONS;

    String PROMPT_4_ENV_NCO = "Please select the Environment from the below options.";

    String PROMPT_4_TXN = "Please enter your transaction name";

    String PROMPT_4_DEPLOY_TYPE =
        "Please select the Deployment Type from the below options (You can enter multiple options separated by Comma)." +
        COMMON_OPTIONS;

    String DEPLOY_REQUEST_SUMMARY = "Deployment Request Summary ::: \n";

    String CONFIRM_DIALOGUE =
        "\n\nPlease confirm your Request by selecting one of the below options. \n1.Proceed with the request. \t2.Let me change the Deployment Request\t" +
        Messages.COMMON_STEPS;

    String PROCESSING_REQUEST = "Fetching Your Transaction...";
        
    String SUBMITTING_REQUEST = "Submitting your request to Queue...";
    
    String SUBMITED_REQUEST = "Your Request is queued up. I will update this chat room once it is picked up for processing.\n";

    String PROCESSING_STATUS_SUCCESS =
        "Your Transaction TXNNAME is fetched successfully, It will be included in the next deployment cycle.";

    String PROCESSING_STATUS_CONFLICT =
        "Your Transaction TXNNAME has non-trivial conflicts with existing deployment requests. Your request will be forwarded to integrators to process it manually.";

    String PROCESSING_STATUS_NOTXN =
        "Transaction TXNNAME is not found in the backend. Please ensure that the transaction is saved and the correct name is provided.";

    String PROCESSING_STATUS_ERROR =
        "Sorry, There was an unexpected error during processing your request. Your request might not be forwarded to integrators, Please report this issue to your integrators";

    String DEPLOYMENT_SUMMARY = "Deployments Summary:";

    String NO_DEPLOYMENTS = "There are no Deployments/Requests as of now\n";

    String PROMPT_4_SOA_COMPS =
        "Please select the SOA Composites to be deployed from the below options." + COMMON_OPTIONS +
        " \n1.All Composites. \t2.Not All Composites - Let me enter the SAR Names";

    String PROMPT_4_SOA_COMPOSITES = "Please enter the SAR Names seperated by comma. " + COMMON_OPTIONS;

//    String PROMPT_4_INTG_TASK =
//        "Please select the Task from the below options." + COMMON_OPTIONS +
//        " \n1.Lock an environment to fetch a TXN manually.  \t\t2.Refresh Memory from DB \n3.Remove a deployment request(Disabled) \t4.Force Build to advance a deployment cycle(Disabled)";

    String PROMPT_4_INTG_TASK =
        "Please select the Task from the below options." + COMMON_OPTIONS +
        " \n1.Lock an environment to fetch a TXN manually.  \t\t2.Refresh Memory from DB";

    String VIEW_LOCKED_SUCCESS =
        "Environment is Locked. Deployment requests for this environment will not be accepted till you unlock it.";

    String PROMPT_4_UNLOCK_VIEW = "Please enter 1 to unlock environment";

    String REFRESHED_MEMORY = "Memory is refreshed";

    String VIEW_UNLOCKED = "Environment is unlocked. I will start accepting deployment requests for this enviroment\n";

    String VIEW_LOCKED =
        "Sorry, Deployment request for this env is not accepted now as integrator has locked this environment. Please try again later\n";

    String SESSION_EXPIRED = "Your session is expired";

    String EXPIRY_WARNING = "Your session will expire in about a minute unless you message me by then.";

    String PROMPT_4_END_DEPLOY_DATE =
        "Please enter the number of days your transaction needs to be included continuously from Today\nValid numbers : 0 to 7 (0 - include only in the next window, 7 - include for seven days from today). " +
        COMMON_OPTIONS;

    String SN_PROMPT_4_ENV = "PROMPT_4_ENV";

    String SN_PROMT_4_TXN = "PROMT_4_TXN";

    String SN_PROMPT_4_DEPLOY_TYPE = "PROMPT_4_DEPLOYMENT_TYPE";

    String SN_PROMPT_4_SOA_COMP_DTLS = "SOA_DEPLOY_DETAILS";

    String SN_PROMPT_4_SOA_COMPS = "PROMPT_4_SOA_COMPOSITES";

    String SN_PROMPT_4_END_DEPLOY_DATE = "PROMPT_4_END_DEPLOY_DATE";

    String SN_DEPLOY_REQUEST_SUMMARY = "DEPLOY_REQUEST_SUMMARY";

    String SN_PROCESS_REQUEST = "PROCESS_REQUEST";

    String SN_PROCESS_STATUS = "PROCESS_STATUS";

    String SN_VIEW_LOCKED = "VIEW_LOCKED";

    String SN_VIEW_UNLOCKED = "VIEW_UNLOCKED";

    String SN_DEPLOYMENTS_SUMMARY = "DEPLOYMENTS_SUMMARY";

    String SN_INTG_TASKS = "INTG_TASKS";

    String SN_LOCK_VIEW = "LOCK_VIEW";

    String SN_UNLOCK_VIEW = "UNLOCK_VIEW";

    String SN_MENU = "MENU";

    String SN_DUMMY = "DUMMY";

    String SN_REFRESH_MEMORY = "REFRESH_MEMORY";

    String ABORT_OPTION = "9. Abort Current Request";

    String PREVIOUS_STEP_OPTION = "8. Go To Previous Step";

    String RESTART_OPTION = "9. Let's Restart from the beginning";

    String COMMON_STEPS = RESTART_OPTION;

    String CLEARED = "Your session is cleared\n";
}

