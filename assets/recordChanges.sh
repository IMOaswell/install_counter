 input=$(git diff --shortstat) 
untracked_files=$(git ls-files --others --exclude-standard) 
if [ -z "$input" ]; then 
input="$(echo "$untracked_files" | wc -l) files" 
fi 
files=$(echo $input | sed -n -E 's/^([0-9]+) file.*/files:\1/p') 
insertions=$(echo $input | sed -n -E 's/.* ([0-9]+) insertion.*/+\1/p') 
deletions=$(echo $input | sed -n -E 's/.* ([0-9]+) deletion.*/-\1/p') 
if [ -z "$insertions" ]; then 
insertions="0" 
fi 
if [ -z "$deletions" ]; then 
deletions="0" 
fi 
output="$files $insertions $deletions" 
echo $output
#a code will be added here that save the output to a file