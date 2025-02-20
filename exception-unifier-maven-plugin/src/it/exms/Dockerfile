FROM wernerdweight/node22:latest

WORKDIR /app
COPY . /app

RUN npm install && npm install -g pm2

CMD ["pm2-runtime", "start", "npm", "--", "run", "start"]

EXPOSE 8080