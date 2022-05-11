def steamInfo = null

// Must be called first before calling other functions
def init(steamCredential, steamCmdPath)
{
   steamInfo = [credential: steamCredential, steamCmd: steamCmdPath]
}

def createDepotManifest(depotID, contentRoot, localPath = "*", depotPath = ".", isRecursive = true, exclude = "*.pdb")
{
   def depotManifest = libraryResource("depot_build_template.vdf")

   depotManifest = depotManifest.replace("<DEPOTID>", depotID)
   depotManifest = depotManifest.replace("<CONTENTROOT>", contentRoot)
   depotManifest = depotManifest.replace("<LOCALPATH>", localPath)
   depotManifest = depotManifest.replace("<DEPOTPATH>", depotPath)
   depotManifest = depotManifest.replace("<ISRECURSIVE>", "${isRecursive ? '1' : '0'}")
   depotManifest = depotManifest.replace("<EXCLUDE>", exclude)

   writeFile(file: "depot_build_${depotID}.vdf", text: depotManifest)
   return "depot_build_${depotID}.vdf"
}

def createAppManifest(appID, depotID, contentRoot, description = "", isPreview = false, localContentPath = "", branch = "", outputDir= "output")
{
   def appManifest = libraryResource("app_build_template.vdf")

   appManifest = appManifest.replace("<APPID>", appID)
   appManifest = appManifest.replace("<DESCRIPTION>", description)
   appManifest = appManifest.replace("<ISPREVIEW>", "${isPreview ? '1' : '0'}")
   appManifest = appManifest.replace("<LOCALCONTENT>", localContentPath)
   appManifest = appManifest.replace("<BRANCH>", branch)
   appManifest = appManifest.replace("<OUTPUTDIR>", outputDir)
   appManifest = appManifest.replace("<CONTENTROOT>", contentRoot)
   appManifest = appManifest.replace("<DEPOTID>", depotID)

   writeFile(file: "app_build_${appID}.vdf", text: appManifest)
   return "app_build_${appID}.vdf"
}

def tryDeploy(appManifest)
{
   try
   {
      log("Trying to deploy to Steam without SteamGuard...")
      deploy(appManifest)
   }
   catch(err)
   {
       try
       {
           log.error("Trying code: H8BBP")
           deploy(appManifest, "H8BBP")
       }
       catch
       {
            try
            {
                 log.error("Trying code: QQRV3")
                 deploy(appManifest, "QQRV3")
            }
            catch
            {
                try
                {
                    log.error("Trying code: M5XYK")
                    deploy(appManifest, "M5XYK")
                }
                catch
                {
                    try
                    {
                        log.error("Trying code: G8DW6")
                        deploy(appManifest, "G8DW6")
                    }
                    catch
                    {
                        try
                        {
                            log.error("Trying code: BWBYM")
                            deploy(appManifest, "BWBYM")
                        }
                        catch
                        {
                            try
                            {
                                log.error("Trying code: R4H7J")
                                deploy(appManifest, "R4H7J")
                            }
                            catch
                            {
                                log.error("Failed to provide Steam Guard code.")
                                return false
                            }
                        }
                    }
                }
            }
       }

      

      

      //def guardCode = null
      //timeout(time: 3, unit: 'MINUTES') 
      //{
      //   guardCode = input message: 'Insert Steam Guard code', ok: 'Submit', 
      //                     parameters: 
      //                     [
      //                        string(name: 'Steam Guard Code', defaultValue: '', description: 'Provide the pipeline with the required Steam Guard code.')
      //                     ]
      //}

      //if (guardCode)
      //{
      //   deploy(appManifest, guardCode)
      //}
      //else
      //{
      //   log.error("Failed to provide Steam Guard code.")
      //   return false
      //}
   }


   return true
}

def deploy(appManifest, steamGuard = null)
{
   withCredentials([usernamePassword(credentialsId: steamInfo.credential, passwordVariable: 'STEAMPASS', usernameVariable: 'STEAMUSER')]) {
        if (steamGuard)
        {
           bat (label: "Deploy to Steam with SteamGuard", script: "\"${steamInfo.steamCmd}\" +login %STEAMUSER% %STEAMPASS% \"${steamGuard}\"  +run_app_build_http \"${appManifest}\" +quit")
        } 
        else 
        {
           bat (label: "Deploy to Steam without SteamGuard", script: "\"${steamInfo.steamCmd}\" +login %STEAMUSER% %STEAMPASS% +run_app_build_http \"${appManifest}\" +quit")
        }
    }
}