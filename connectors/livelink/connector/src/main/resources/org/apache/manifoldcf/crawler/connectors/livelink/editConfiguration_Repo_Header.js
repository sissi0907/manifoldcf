<script type="text/javascript">
    <!--
    function ServerDeleteCertificate(aliasName)
    {
      editconnection.serverkeystorealias.value = aliasName;
      editconnection.serverconfigop.value = "Delete";
      postForm();
    }

    function ServerAddCertificate()
    {
      if (editconnection.servercertificate.value == "")
      {
        alert("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.ChooseACertificateFile'))");
        editconnection.servercertificate.focus();
      }
      else
      {
        editconnection.serverconfigop.value = "Add";
        postForm();
      }
    }

    function IngestDeleteCertificate(aliasName)
    {
      editconnection.ingestkeystorealias.value = aliasName;
      editconnection.ingestconfigop.value = "Delete";
      postForm();
    }

    function IngestAddCertificate()
    {
      if (editconnection.ingestcertificate.value == "")
      {
        alert("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.ChooseACertificateFile'))");
        editconnection.ingestcertificate.focus();
      }
      else
      {
        editconnection.ingestconfigop.value = "Add";
        postForm();
      }
    }

    function checkConfig()
    {
      if (editconnection.serverport.value != "" && !isInteger(editconnection.serverport.value))
      {
        alert("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.AValidNumberIsRequired'))");
        editconnection.serverport.focus();
        return false;
      }
      if (editconnection.ingestport.value != "" && !isInteger(editconnection.ingestport.value))
      {
        alert("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.AValidNumberOrBlankIsRequired'))");
        editconnection.ingestport.focus();
        return false;
      }
      if (editconnection.viewport.value != "" && !isInteger(editconnection.viewport.value))
      {
        alert("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.AValidNumberOrBlankIsRequired'))");
        editconnection.viewport.focus();
        return false;
      }
      return true;
    }

    function checkConfigForSave()
    {
      if (editconnection.servername.value == "")
      {
        alert("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.EnterALivelinkServerName'))");
        SelectTab("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.Server'))");
        editconnection.servername.focus();
        return false;
      }
      if (editconnection.serverport.value == "")
      {
        alert("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.AServerPortNumberIsRequired'))");
        SelectTab("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.Server'))");
        editconnection.serverport.focus();
        return false;
      }
      if (editconnection.serverhttpcgipath.value == "")
      {
        alert("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.EnterTheServerCgiPathToLivelink'))");
        SelectTab("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.Server'))");
        editconnection.serverhttpcgipath.focus();
        return false;
      }
      if (editconnection.serverhttpcgipath.value.substring(0,1) != "/")
      {
        alert("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.TheServerCgiPathMustBeginWithACharacter'))");
        SelectTab("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.Server'))");
        editconnection.serverhttpcgipath.focus();
        return false;
      }
      if (editconnection.viewprotocol.value == "" && editconnection.ingestprotocol.value == "")
      {
        alert("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.SelectAViewProtocol'))");
        SelectTab("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.DocumentView'))");
        editconnection.viewprotocol.focus();
        return false;
      }
      if (editconnection.viewcgipath.value == "" && editconnection.ingestcgipath.value == "")
      {
        alert("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.EnterTheViewCgiPathToLivelink'))");
        SelectTab("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.DocumentView'))");
        editconnection.viewcgipath.focus();
        return false;
      }
      if (editconnection.ingestcgipath.value != "" && editconnection.ingestcgipath.value.substring(0,1) != "/")
      {
        alert("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.TheIngestCgiPathMustBeBlankOrBeginWithACharacter'))");
        SelectTab("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.DocumentAccess'))");
        editconnection.ingestcgipath.focus();
        return false;
      }
      if (editconnection.viewcgipath.value != "" && editconnection.viewcgipath.value.substring(0,1) != "/")
      {
        alert("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.TheViewCgiPathMustBeBlankOrBeginWithACharacter'))");
        SelectTab("$Encoder.bodyJavascriptEscape($ResourceBundle.getString('LivelinkConnector.DocumentView'))");
        editconnection.viewcgipath.focus();
        return false;
      }
      return true;
    }

    //-->
</script>