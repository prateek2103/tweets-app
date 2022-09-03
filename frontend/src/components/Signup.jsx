import React, { useRef } from "react";
import { useState } from "react";
import { toast } from "react-toastify";
import { registerUser } from "../context/tweetsAction";
import AWS from "aws-sdk";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";

window.Buffer = window.Buffer || require("buffer").Buffer;

function Signup() {
  const avatarRef = useRef();
  const formRef = useRef();
  const navigate = useNavigate();

  const [formState, setFormState] = useState({
    firstname: "",
    lastname: "",
    phone: "",
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
  });

  //AWS config
  AWS.config.update({
    accessKeyId: process.env.REACT_APP_ACCESS_KEY_ID,
    secretAccessKey: process.env.REACT_APP_SECRET_ACCESS_KEY,
  });

  //aws s3 url
  const s3Url =
    "https://" +
    process.env.REACT_APP_S3_BUCKET_NAME +
    ".s3." +
    process.env.REACT_APP_S3_REGION +
    ".amazonaws.com/";

  //validation of form details
  const validateForm = () => {
    let shouldSubmit = true;
    let errorMessage = "";

    //confirm password and password should match
    if (formState.confirmPassword !== formState.password) {
      shouldSubmit = false;
      errorMessage = "confirm password and password do not match";
    }

    //contact number should be 10 digits
    if ((formState.phone + "").length != 10) {
      shouldSubmit = false;
      errorMessage = "contact no. should have 10 digits";
    }

    return { shouldSubmit, errorMessage };
  };

  //method to submit form details
  const onSubmitHandler = (e) => {
    e.preventDefault();
    let { shouldSubmit, errorMessage } = validateForm();

    //if valid
    if (shouldSubmit) {
      //save the avatar in s3 bucket
      let file = avatarRef.current.files[0];

      //create the s3 bucket
      const myBucket = new AWS.S3({
        region: process.env.REACT_APP_S3_REGION,
      });

      //bucket params
      const params = {
        Body: file,
        Bucket: process.env.REACT_APP_S3_BUCKET_NAME,
        Key: file.name,
      };

      myBucket
        .putObject(params)
        .on("success", (data) => {
          //prepare request body
          let reqBody = {
            firstName: formState.firstname,
            lastName: formState.lastname,
            contactNumber: formState.phone,
            username: formState.username,
            password: formState.password,
            email: formState.email,
            avatarUrl: s3Url + file.name,
          };

          //register the user
          registerUser(reqBody)
            .then((res) => {
              toast.success(res.data);
              navigate("/login");
            })
            .catch((err) => {
              toast.error(err.response.data.errorMessage);
            });
        })
        .send((err) => {
          if (err) toast.error("Server error. Please try again later");
        });
    }

    //incase of form errors
    if (!shouldSubmit) toast.error(errorMessage);
  };

  //method to track changes in form data
  const onChangeHandler = (e) => {
    const { name, value } = e.target;
    setFormState({
      ...formState,
      [name]: value,
    });
  };

  return (
    <div>
      <form className="px-10 pt-[7%]" onSubmit={onSubmitHandler} ref={formRef}>
        <div className="flex flex-row justify-between mb-5">
          <div className="flex-1">
            <input
              name="firstname"
              value={formState.firstname}
              className="input-lg border-2 border-slate-200 w-[90%] rounded-full mr-4 mb-4"
              placeholder="firstname"
              type="text"
              onChange={onChangeHandler}
              required
            />
          </div>
          <div className="flex-1">
            <input
              name="lastname"
              value={formState.lastname}
              className="input-lg border-2 border-slate-200 w-[90%] rounded-full mr-4 mb-4"
              placeholder="lastname"
              onChange={onChangeHandler}
              type="text"
            />
          </div>
        </div>

        <div className="flex flex-row justify-between  mb-5">
          <div className="flex w-100">
            <input
              name="phone"
              value={formState.phone}
              className="input-lg border-2 border-slate-200 w-[100%] rounded-full mr-4 mb-4"
              placeholder="phone"
              onChange={onChangeHandler}
              type="number"
              required
            />
          </div>
          <div className="flex-1 ">
            <input
              name="email"
              value={formState.email}
              className="input-lg border-2 border-slate-200 w-[100%] rounded-full mr-4 mb-4"
              placeholder="email"
              onChange={onChangeHandler}
              type="email"
              required
            />
          </div>
        </div>

        <div>
          <b>
            <label className="label" for="file">
              Upload avatar
            </label>
          </b>
          <input
            name="avatarUrl"
            ref={avatarRef}
            className="input mr-4 mb-4 p-2"
            placeholder="avatar"
            type="file"
            accept="image/png, image/jpeg"
          />
        </div>

        <input
          name="username"
          value={formState.username}
          className="input-lg border-2 border-slate-200 w-[70%] rounded-full mr-4 mb-10"
          placeholder="username"
          onChange={onChangeHandler}
          type="text"
          required
        />

        <input
          name="password"
          value={formState.password}
          className="input-lg border-2 border-slate-200 w-full rounded-full mb-10"
          placeholder="password"
          onChange={onChangeHandler}
          type="password"
          required
        />

        <input
          name="confirmPassword"
          value={formState.confirmPassword}
          className="input-lg border-2 border-slate-200 w-full rounded-full mb-8"
          placeholder="confirm password"
          onChange={onChangeHandler}
          type="password"
          required
        />

        <button className="btn w-4/12 mt-2 transition ease-linear hover:bg-tweeter-blue">
          Sign Up
        </button>

        <Link to="/login" className="btn btn-primary w-3/12 ml-2 mt-2">
          Back
        </Link>
      </form>
    </div>
  );
}

export default Signup;
