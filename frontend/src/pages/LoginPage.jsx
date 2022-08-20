import SignIn from "../components/SignIn";
import React from "react";
import { Link } from "react-router-dom";

function LoginPage() {
  return (
    <div>
      <div className="grid grid-cols-12 min-w-screen">
        <div className="col-span-8">
          <div className="bg-black hero min-h-screen ">
            <div className="hero-content">
              <div className="text-center">
                <h1 className="text-[12rem] text-white font-thin">Tweeter</h1>
                <p className="text-xl text-white">
                  See what's happening around you
                </p>
              </div>
            </div>
          </div>
        </div>
        <div className="col-span-4">
          <div className="px-10 pt-[25%]">
            <SignIn />
          </div>
          <div className="text-center pt-10">
            <span className="text-xl block">New here? No worries</span>
            <Link
              className="text-xl text-tweeter-blue hover:font-bold"
              to="/signup"
            >
              Sign up
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
