import axios from "axios";
import React from "react";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import {
  deleteTweet,
  editTweet,
  getTweetsByUsername,
} from "../context/tweetsAction";

function TweetsByUsername({ username }) {
  const [tweets, setTweets] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    //get all the tweets by the user
    getTweetsByUsername(username)
      .then((res) => {
        setTweets(res.data);
        setIsLoading(false);
      })
      .catch((err) => {
        setIsLoading(false);
      });
  }, []);

  //delete tweet handler
  const onTweetDeleteHandler = (id) => {
    deleteTweet(id)
      .then((res) => {
        let filteredTweets = tweets.filter((tweet) => tweet.id != id);
        setTweets(filteredTweets);
        toast.success("tweet deleted successfully");
      })
      .catch((err) => {
        toast.error("Please try again later");
      });
  };

  //edit tweet handler
  const onTweetEditHandler = (id) => {
    const textBox = document.getElementById(id);
    const submitButton = document.getElementById(id + "Edit");

    //toggle box value to submit or edit based on actions
    if (isSubmitting === false) {
      submitButton.innerText = "submit";
      textBox.disabled = false;
      setIsSubmitting(true);
    } else {
      const oldValue = textBox.value;
      setIsSubmitting(false);
      textBox.disabled = true;
      submitButton.innerText = "edit";

      //update the tweet
      editTweet(id, textBox.value)
        .then((res) => {
          toast.success("tweet updated successfully");
        })
        .catch((err) => {
          toast.error("Please try again later");
          textBox.value = oldValue;
        });
    }
  };

  //method to retrieve date of the tweet
  const getDateOfTweet = (tweetDate) => {
    let dateDiff = new Date() - new Date(tweetDate);
    let seconds = Math.floor(dateDiff / 1000);
    let minutes = Math.floor(seconds / 60);
    let hours = Math.floor(minutes / 60);
    let days = Math.floor(hours / 24);

    if (days >= 1) {
      return days == 1 ? days + " day ago" : days + " days ago";
    } else if (hours >= 1) {
      return hours == 1 ? hours + " hour ago" : hours + " hours ago";
    } else if (minutes >= 1) {
      return minutes == 1 ? minutes + " minute ago" : minutes + " minutes ago";
    } else {
      return seconds == 1 ? seconds + " second ago" : seconds + " seconds ago";
    }
  };

  return (
    <div className="mt-5">
      {!isLoading && tweets.length > 0 && (
        <div class="overflow-y-scroll w-full h-[300px]">
          <table class="table w-full">
            <tbody>
              {tweets.map((tweet) => {
                return (
                  <tr>
                    <td>
                      <div className="w-full px-2 py-2 grid grid-cols-12">
                        <div className="col-span-2">
                          <div class="avatar">
                            <div class="w-24 rounded-full">
                              <img src={tweet.avatarUrl} />
                            </div>
                          </div>
                        </div>
                        <div className="col-span-10">
                          <span className="float-right block">
                            {getDateOfTweet(tweet.createdAt)}
                          </span>
                          <h1>@{tweet.handle}</h1>

                          <textarea
                            id={tweet.id}
                            class="textarea w-3/4 resize-none mt-2"
                            disabled="true"
                          >
                            {tweet.message}
                          </textarea>

                          {tweet.handle === localStorage.getItem("username") ? (
                            <>
                              <button
                                className="btn btn-sm btn-error float-right mt-12"
                                onClick={() => {
                                  onTweetDeleteHandler(tweet.id);
                                }}
                              >
                                delete
                              </button>
                              <button
                                id={tweet.id + "Edit"}
                                className="btn btn-sm btn-info float-right mt-12 mr-1"
                                onClick={() => onTweetEditHandler(tweet.id)}
                              >
                                edit
                              </button>
                            </>
                          ) : (
                            <></>
                          )}
                        </div>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      {!isLoading && tweets.length == 0 && (
        <h1 className="font-thin text-3xl text-center mt-10 ">No tweets yet</h1>
      )}

      {isLoading && (
        <span className="mt-5 btn btn-primary bg-tweeter-blue">Loading...</span>
      )}
    </div>
  );
}

export default TweetsByUsername;
