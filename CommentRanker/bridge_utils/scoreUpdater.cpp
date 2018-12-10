#include<bits/stdc++.h>
using namespace std;

vector<pair<int, int>>comment_scores;
unordered_map<int, int>updated_scores;

int main (){
  freopen ("commentsChanged.txt", "r", stdin);

  int comment_id;
  int comment_score;
  string comment;

  while (cin >> comment_id >> comment_score && comment_id != EOF) {
    comment_scores.push_back({comment_score, comment_id});
    updated_scores[comment_id] = comment_score;
  }

  cin.clear();

  freopen ("commentsSortedByRelevance", "r", stdin);
  freopen ("finalCommentScores.txt", "w", stdout);

  while (cin >> comment_id >> comment_score && getline(cin, comment) && comment_id != EOF) {
    if (updated_scores.find(comment_id) == updated_scores.end())
      comment_scores.push_back({comment_score, comment_id});
  }

  sort (comment_scores.begin(), comment_scores.end());

  for (int i = 0; i < comment_scores.size(); i++){
    cout << comment_scores[i].second << " " << comment_scores[i].first << endl;
  }

  return 0;
}
