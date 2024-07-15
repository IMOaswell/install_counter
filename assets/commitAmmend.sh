echo Commit Ammend 
git log -1 --format=%s 
echo 'are u sure? (press any key to confirm)' 
read userInput 
git add . 
git commit --amend --no-edit