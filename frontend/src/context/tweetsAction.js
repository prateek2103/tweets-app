import axios from "axios";

//constants
const TWEETS_URL = process.env.REACT_APP_TWEETS_URL;

//axios config
const tweets = axios.create({
  baseURL: TWEETS_URL,
});

//login user
export const loginUser = async (username, password) => {
  const response = await tweets.post("/login", {
    username: username,
    password: password,
  });

  return response;
};

//register user
export const registerUser = async (reqBody) => {
  const response = await tweets.post("/register", reqBody);
  return response;
};

//forgot password
export const changePassword = async (password) => {
  const config = {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token"),
      "Content-Type": "text/plain",
    },
  };

  let response = await tweets.post(
    "/" + localStorage.getItem("username") + "/forgot",
    password,
    config
  );

  return response;
};

//post tweet
export const postTweet = async (reqBody) => {
  const config = {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  };
  let response = await tweets.post(
    "/" + localStorage.getItem("username") + "/add",
    reqBody,
    config
  );

  return response;
};

//load tweets by username
export const getTweetsByUsername = async (username) => {
  const config = {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  };

  let response = await tweets.get("/" + username, config);
  return response;
};

//edit tweet
export const editTweet = async (tweetId, tweetMessage) => {
  const config = {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  };

  let response = await tweets.put(
    "/" + localStorage.getItem("username") + "/update/" + tweetId,
    { message: tweetMessage },
    config
  );

  return response;
};

//delete tweet
export const deleteTweet = async (tweetId) => {
  const config = {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  };

  let response = await tweets.delete(
    "/" + localStorage.getItem("username") + "/delete/" + tweetId,
    config
  );

  return response;
};

//get all tweets
export const getAllTweets = async () => {
  const config = {
    headers: { Authorization: "Bearer " + localStorage.getItem("token") },
  };
  let response = await tweets.get("/all", config);
  return response;
};

//reply to a tweet
export const replyTweet = async (tweetId, replyMessage) => {
  const config = {
    headers: { Authorization: "Bearer " + localStorage.getItem("token") },
  };
  let response = await tweets.post(
    "/" + localStorage.getItem("username") + "/reply/" + tweetId,
    {
      message: replyMessage,
      handle: localStorage.getItem("username"),
      createdAt: new Date(),
    },
    config
  );

  return response;
};

//like tweet
export const likeTweet = async (tweetId) => {
  const config = {
    headers: { Authorization: "Bearer " + localStorage.getItem("token") },
  };
  let response = await tweets.post(
    "/" + localStorage.getItem("username") + "/like/" + tweetId,
    {},
    config
  );
  return response;
};

//get all users
export const getAllUsers = async () => {
  const config = {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  };

  let response = await tweets.get("/users/all", config);
  return response;
};

//get users by username
export const getUsersByUsername = async (username) => {
  const config = {
    headers: {
      Authorization: "Bearer " + localStorage.getItem("token"),
    },
  };

  let response = tweets.get("/user/search/" + username, config);
  return response;
};
