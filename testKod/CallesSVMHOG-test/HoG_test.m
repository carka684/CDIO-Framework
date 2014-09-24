D = dir('*.png');
size = 500;
imcell = cell(1,numel(D));
for i = 1:numel(D)
  imcell{i} = imresize(imread(D(i).name),[size size]);
end

%%

hogVector = [];
for k = 1:numel(D)
    hogVector  = [hogVector; extractHOGFeatures(imcell{k})];
end

%%
class = [ones(1,7), -1*ones(1,6)]';
SVMModel = fitcsvm(hogVector,class,'KernelFunction','linear');

%%
testApple = imresize(imread('testbilder/apple_test.png'),[size size]);
appleHog = extractHOGFeatures(testApple);
testBanan = imresize(imread('testbilder/banan_test.png'),[size size]);
bananHog = extractHOGFeatures(testBanan);
[Apple AppleScore] = predict(SVMModel,appleHog)
[Banan BananaScore] = predict(SVMModel,bananHog)