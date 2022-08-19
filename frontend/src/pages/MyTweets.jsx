import React from "react";

function MyTweets() {
  return (
    <div className="mt-5">
      <div class="overflow-y-scroll w-full h-[300px]">
        <table class="table w-full">
          <tbody>
            <tr>
              <td>
                <div className="w-full px-5 py-2 grid grid-cols-12">
                  <div className="col-span-2">
                    <div class="avatar">
                      <div class="w-24 rounded-full">
                        <img src="https://placeimg.com/192/192/people" />
                      </div>
                    </div>
                  </div>
                  <div className="col-span-10">
                    <span className="float-right block">3 hrs ago</span>
                    <h1>@UserHandle</h1>

                    <textarea
                      class="textarea w-3/4 resize-none mt-2"
                      value="this is my first tweet"
                      disabled
                    ></textarea>
                    <a className="float-right mt-12 text-red-500">delete</a>
                    <a className="float-right mt-12 mr-5 text-tweeter-blue">
                      edit
                    </a>
                  </div>
                </div>
              </td>
            </tr>
            <tr>
              <td>
                <div className="w-full px-5 py-2 grid grid-cols-12">
                  <div className="col-span-2">
                    <div class="avatar">
                      <div class="w-24 rounded-full">
                        <img src="https://placeimg.com/192/192/people" />
                      </div>
                    </div>
                  </div>
                  <div className="col-span-10">
                    <span className="float-right block">3 hrs ago</span>
                    <h1>@UserHandle</h1>

                    <textarea
                      class="textarea w-3/4 resize-none mt-2"
                      value="this is my first tweet"
                      disabled
                    ></textarea>
                    <a className="float-right mt-12 text-red-500">delete</a>
                    <a className="float-right mt-12 mr-5 text-tweeter-blue">
                      edit
                    </a>
                  </div>
                </div>
              </td>
            </tr>
            <tr>
              <td>
                <div className="w-full px-5 py-2 grid grid-cols-12">
                  <div className="col-span-2">
                    <div class="avatar">
                      <div class="w-24 rounded-full">
                        <img src="https://placeimg.com/192/192/people" />
                      </div>
                    </div>
                  </div>
                  <div className="col-span-10">
                    <span className="float-right block">3 hrs ago</span>
                    <h1>@UserHandle</h1>

                    <textarea
                      class="textarea w-3/4 resize-none mt-2"
                      value="this is my first tweet"
                      disabled
                    ></textarea>
                    <a className="float-right mt-12 text-red-500">delete</a>
                    <a className="float-right mt-12 mr-5 text-tweeter-blue">
                      edit
                    </a>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default MyTweets;
