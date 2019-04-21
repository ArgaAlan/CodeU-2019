/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/** Fetches messages and add them to the page. */
function fetchMessages(){
  const url = '/feed';
  fetch(url).then((response) => {
    return response.json();
  }).then((messages) => {
    const messageContainer = document.getElementById('message-container');
    if(messages.length == 0){
     messageContainer.innerHTML = '<p>There are no posts yet.</p>';
    }
    else{
     messageContainer.innerHTML = '';
    }
    messages.forEach((message) => {
     const messageDiv = buildMessageDiv(message);
     messageContainer.appendChild(messageDiv);
    });
  });
}

/**
 * Builds an element that displays the message.
 * @param {Message} message
 * @return {Element}
 */
 function buildMessageDiv(message) {
   const headerDiv = document.createElement('div');
   headerDiv.classList.add('message-header');
   headerDiv.appendChild(document.createTextNode("User: "));

   const userLink = document.createElement('a');
   userLink.href = "/users/" + message.user;
   userLink.innerHTML = message.user;
   headerDiv.appendChild(userLink);
   headerDiv.appendChild(document.createTextNode(' - ' + "Country: "));

   const countryLink = document.createElement('a');
   countryLink.href = "/country/" + message.country;
   countryLink.innerHTML = message.country;
   headerDiv.appendChild(countryLink);
   headerDiv.appendChild(document.createTextNode(' - ' +
   new Date(message.timestamp)));

   const bodyDiv = document.createElement('div');
   bodyDiv.classList.add('message-body');
   bodyDiv.innerHTML = message.text;
   if (message.imageUrl != null || !message.imageUrl != "") {
     bodyDiv.innerHTML += '<br/>';
     bodyDiv.innerHTML += '<img src="' + message.imageUrl + '" />';
   }

   const replyForm = document.createElement('form');
   replyForm.classList.add('message-form-button');
   replyForm.action = "/thread/" + message.id.toString();
   replyForm.id = 'reply-form';
   const replyButton = document.createElement('button');
   replyButton.type = "submit";
   replyButton.value = "Submit";
   replyButton.innerHTML = "See Thread or Reply";
   replyForm.appendChild(replyButton);

   const messageDiv = document.createElement('div');
   messageDiv.classList.add('message-div');
   messageDiv.appendChild(headerDiv);
   messageDiv.appendChild(bodyDiv);
   messageDiv.appendChild(replyForm);

   return messageDiv;
 }


/** Fetches data and populates the UI of the page. */
function buildUI(){
 fetchMessages();
}
