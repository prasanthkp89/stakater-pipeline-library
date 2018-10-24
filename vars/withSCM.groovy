def call(body) {
    def utils = new io.fabric8.Utils()
    def git = new io.stakater.vc.Git()
    def flow = new io.stakater.StakaterCommands()

    def scmConfig = scm.getUserRemoteConfigs()[0]
    
    def repoUrl = scmConfig.getUrl()
    def tokenizedUrl = repoUrl.tokenize('/')
    def repoName = tokenizedUrl.last().split('\\.git').first()
    def repoOwner = tokenizedUrl.get(tokenizedUrl.size() - 2)

    echo "INSIDE SCM"
    echo "Branch Name: ${env.BRANCH_NAME}"

    def projectUrl = flow.getScmPushUrl()
    echo "URL: ${projectUrl}"
    def provider = flow.getProvider(projectUrl)
    echo "provider: ${provider}"

    def project = flow.getProject(provider)
    echo "project name with organization: ${project}"

    def providerToken = flow.getProviderToken(provider)
    echo "provider-token: ${providerToken}"

    if(!repoUrl.startsWith("git@")) {
        // Lets make it ssh url link
        def url = new java.net.URL(repoUrl)

        repoUrl = "git@${url.getHost()}:${url.getPath().substring(1)}" 
    }
    
    if (env.CHANGE_FORK) {
        repoUrl = repoUrl.replaceFirst(repoOwner, env.CHANGE_FORK)
    }

    def repoBranch = ""

    if(utils.getBranch().startsWith("PR-")) {
        // Fetch branch from CHANGE_AUTHOR
        repoBranch = "${env.CHANGE_BRANCH}"
    }
    else {
        //repoBranch = scmConfig.getRefspec().tokenize('/').last()
        repoBranch = utils.getBranch()
    }
    withEnv(["REPO_URL=${repoUrl}",
             "REPO_NAME=${repoName}",
             "REPO_OWNER=${repoOwner}",
             "REPO_BRANCH=${repoBranch}"]) {
        body(repoUrl, repoName, repoOwner, repoBranch)
    }
}