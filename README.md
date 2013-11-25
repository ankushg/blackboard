Instructions for the GitHub Repo
===
## Setting up GitHub Remote
Add a remote repository:

    git remote add github git@github.com:ankushg/blackboard.git

Push to GitHub manually via:

    git push github

## Pushing to GitHub and 6.005 Repos Simultaneously
### Option 1: Add GitHub to as a Push URL to Origin (Recommended)
Run this command to automatically push to GitHub when you do a `git push` (replace `<kerberos>` in the first line with your actual kerberos):

    git remote set-url --add --push origin git@github.com:ankushg/blackboard.git
    git remote set-url --add --push origin ssh://<kerberos>@athena.dialup.mit.edu/afs/athena.mit.edu/course/6/6.005/git/fa13/projects/whiteboard/ankush-jlmart88-kevinwen.git

### Option 1: Set up a separate remote that includes both GitHub and 6.005
Automatically push to GitHub and the official 6.005 repo at the same time by doing the following (replace `<kerberos>` in the first line with your actual kerberos):

    git remote set-url --add --push origin ssh://<kerberos>@athena.dialup.mit.edu/afs/athena.mit.edu/course/6/6.005/git/fa13/projects/whiteboard/ankush-jlmart88-kevinwen.git
    git config --add remote.all.url git@github.com:ankushg/blackboard.git

You can now do `git push all` to push to both 6.005 and GitHub (but will require your password still for pushing to 6.005).
