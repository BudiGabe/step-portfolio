// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Fetch list of comments from server and print them on page
 */
function getComments() {
    const maxComms = document.getElementById("max-comments").value;

    fetch("/data?maxComms=" + maxComms).then(response => response.json()).then((comments) => {
        const commentsContainer = document.getElementById("comments-container");
        commentsContainer.innerHTML = '';
        for(const comment of comments) {
            commentsContainer.appendChild(createListElement(comment.message, comment.score));
        }
    })
}

function deleteComments() {
  fetch("/delete-data", {method: 'post'}).then(getComments());
}

function createListElement(message, score) {
  const liElement = document.createElement('li');
  liElement.innerText = message + ' (' + decideMorality(score) + ')';
  return liElement;
}

function decideMorality(score) {
  return score > 0 ? "good" : "evil";
}