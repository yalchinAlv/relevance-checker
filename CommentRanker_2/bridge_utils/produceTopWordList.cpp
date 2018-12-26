#include<bits/stdc++.h>
using namespace std;

const int REDUCER_NO = 2;
const string FILE_PATH[] = {"../temp_dir/part-00000", "../temp_dir/part-00001"};
const int FILE_PATH_LENGTH = 22;

int main() {
    freopen ("key-score.txt", "w", stdout);

    vector< pair<int, string> >key_score;

    char** filePath = new char*[REDUCER_NO];
    for (int i = 0; i < REDUCER_NO; i++)
        filePath[i] = new char[FILE_PATH_LENGTH];
    
    for (int i = 0; i < REDUCER_NO; i++)
        for (int j = 0; j < FILE_PATH[i].size(); j++)
            filePath[i][j] = FILE_PATH[i][j]; 

    int count = 0;
    while (count < REDUCER_NO){
        cin.clear();
        freopen (filePath[count], "r", stdin);
        
        string line;
        while (getline(cin, line)){
            int score = 1;
            string word = "";
            for (int i = 0; i < line.size(); i++){
                if (line[i] == '\t')
                    break;
                word.append(1u, line[i]);
            }
            for (int i = 0; i < line.size(); i++)
                if (line[i] == ' ')
                    score++;
            key_score.push_back({score, word});
        }
        count++;
    }
    
    sort(key_score.begin(), key_score.end());
    reverse(key_score.begin(), key_score.end());

    for (int i = 0; i < key_score.size(); i++){
        cout << key_score[i].second << " " << key_score[i].first << endl;
    }
    
    return 0;
}
