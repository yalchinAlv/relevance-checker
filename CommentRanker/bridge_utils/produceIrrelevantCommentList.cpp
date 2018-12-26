#include<bits/stdc++.h>
using namespace std;

const int MAX_COMMENT_NO = 100000;

const int REDUCER_NO = 2;
const string FILE_PATH[] = {"../output_dir/part-00000", "../output_dir/part-00001"};
const int FILE_PATH_LENGTH = 25;

unordered_map<int, string>comments;
vector< pair<int, int> >comment_scores;
bool used[MAX_COMMENT_NO];

int main() {

    freopen("../input_dir/comments.txt", "r", stdin);

    string comment;
    int comment_id;

    while (cin >> comment_id && getline(cin, comment)){
        comments[comment_id] = comment;
    }

    char** filePath = new char*[REDUCER_NO];
    for (int i = 0; i < REDUCER_NO; i++)
        filePath[i] = new char[FILE_PATH_LENGTH];
    
    for (int i = 0; i < REDUCER_NO; i++)
        for (int j = 0; j < FILE_PATH[i].size(); j++)
            filePath[i][j] = FILE_PATH[i][j]; 

    int sum = 0;
    int count = 0;
    freopen ("irrelevant_comment-score.txt", "w", stdout);
    while(count < REDUCER_NO){
      cin.clear();

      freopen (filePath[count], "r", stdin);

      int comment_score;
      while ((cin >> comment_id >> comment_score) && comment_id != EOF){
        comment_scores.push_back({comment_score, comment_id});
        sum += comment_score;
      }
      count++;
    }

    for (int i = 0; i < MAX_COMMENT_NO; i++)
      used[i] = false;
    for (int i = 0; i < comment_scores.size(); i++)
      used[comment_scores[i].second] = true;

    unordered_map<int, string>::iterator it;
    for (it = comments.begin(); it != comments.end(); it++)
      if (!used[it->first])
        comment_scores.push_back({0, it->first});

    sort(comment_scores.begin(), comment_scores.end());

    int threshold = sum / comment_scores.size();

    for (int i = 0; i < comment_scores.size(); i++) {
      if (comment_scores[i].first >= threshold)
        break;
      cout << comment_scores[i].second << ' ' << comment_scores[i].first << " <" << comments[comment_scores[i].second].substr(1) << ">" << endl;
    }

    freopen ("commentsSortedByRelevance.txt", "w", stdout);

    for (int i = 0; i < comment_scores.size(); i++) {
      cout << comment_scores[i].second << ' ' << comment_scores[i].first << " <" << comments[comment_scores[i].second].substr(1) << ">" << endl;
    }

    return 0;
}
