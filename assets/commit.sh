 echo Enter Commit Message 
nothing="probably just testing:D" 
echo put nothing to set it to \"$nothing\" 
echo commit message: 
read userInput 
git add . 
if [ -z "$userInput" ]; then 
    echo "$nothing" 
    git commit -m "$nothing" 
else 
    git commit -m "$userInput"
fi
